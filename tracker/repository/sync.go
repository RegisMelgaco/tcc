package repository

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"local/tracker"
	"local/tracker/logs"
	"log/slog"
	"slices"
	"time"
)

type SyncInput struct {
	tracker.Node
	NetworkSecret string `json:"secret"`
}

func (r *Repository) Sync(ctx context.Context, input SyncInput) ([]tracker.Node, error) {
	const operation = "Repository.Sync"

	row := r.db.QueryRowContext(ctx, selectNetwork, input.NetworkSecret)
	if err := row.Err(); err != nil {
		return nil, fmt.Errorf("%s: %w", operation, err)
	}

	var networkID int
	if err := row.Scan(&networkID); err != nil {
		return nil, fmt.Errorf("%s: %w", operation, err)
	}

	newNodes, err := r.queryByUpdatedAt(ctx, input.UpdatedAt, input.Email, networkID)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", operation, err)
	}

	result := make([]tracker.Node, 0, len(newNodes))
	for _, n := range newNodes {
		result = append(result, tracker.Node{
			Email:     n.email.String,
			PublicIP:  n.publicIP.String,
			LocalIPs:  n.localIPs(),
			UpdatedAt: int(n.updatedAt.Int64),
		})
	}

	if result == nil {
		result = []tracker.Node{}
	}

	_, err = r.insert(ctx, insertInput{
		email:     input.Email,
		publicIP:  input.PublicIP,
		localIPs:  input.LocalIPs,
		networkID: networkID,
	})

	if err != nil {
		return nil, fmt.Errorf("%s: %w", operation, err)
	}

	return result, nil
}

type insertInput struct {
	email     string
	publicIP  string
	localIPs  []string
	networkID int
}

func (i insertInput) equals(n node) bool {
	return n.email.String == i.email &&
		n.publicIP.String == i.publicIP &&
		slices.Equal(n.localIPs(), i.localIPs)
}

func (r *Repository) insert(ctx context.Context, input insertInput) (node, error) {
	const operation = "Repository.insert"

	logger := logs.FromContext(ctx, operation).With(slog.Time("start", time.Now()))

	stored, err := r.queryNodeByEmail(ctx, input.email, input.networkID)
	if err != nil {
		if !errors.Is(err, sql.ErrNoRows) {
			return node{}, fmt.Errorf("%s: %w", operation, err)
		}
	}

	if input.equals(stored) {
		return stored, nil
	}

	tx, err := r.db.BeginTx(ctx, nil)
	if err != nil {
		logger.Error("start transaction failed", slog.String("err", err.Error()))

		return node{}, fmt.Errorf("%s: %w", operation, err)
	}

	defer func() {
		err := tx.Rollback()
		if err != nil && !errors.Is(err, sql.ErrTxDone) {
			logger.Error("rollback failed", slog.String("err", err.Error()))
		}
	}()

	stmt, err := tx.PrepareContext(ctx, deleteLocalIPs)
	if err != nil {
		logger.Error("prepare delete local IPs statement failed", slog.String("query", insertNode), slog.String("err", err.Error()))

		return node{}, fmt.Errorf("%s: %w", operation, err)
	}

	_, err = stmt.ExecContext(ctx, stored.id.Int64)
	if err != nil {
		logger.Error("delete local IPs failed", slog.String("query", deleteLocalIPs), slog.String("err", err.Error()))

		return node{}, fmt.Errorf("%s: %w", operation, err)
	}

	stmt, err = tx.PrepareContext(ctx, insertNode)
	if err != nil {
		logger.Error("prepare insert node statement failed", slog.String("query", insertNode), slog.String("err", err.Error()))

		return node{}, fmt.Errorf("%s: %w", operation, err)
	}

	defer stmt.Close()

	row := stmt.QueryRowContext(ctx, input.publicIP, time.Now().Unix(), input.email, input.networkID)
	if err = row.Scan(&stored.id, &stored.updatedAt); err != nil {
		logger.Error("insert node failed", slog.String("query", insertNode), slog.String("err", err.Error()))

		return node{}, fmt.Errorf("%s: %w", operation, err)
	}

	for _, li := range input.localIPs {
		if _, err = tx.ExecContext(ctx, insertLocalIP, stored.id, li); err != nil {
			logger.Error("insert local ip failed", slog.String("query", insertLocalIP), slog.String("err", err.Error()))

			return node{}, fmt.Errorf("%s: %w", operation, err)
		}
	}

	if err = tx.Commit(); err != nil {
		logger.Error("commit transaction failed", slog.String("err", err.Error()))

		return node{}, fmt.Errorf("%s: %w", operation, err)
	}

	return stored, nil
}

func (r *Repository) queryNodeByEmail(ctx context.Context, email string, networkID int) (node, error) {
	const operation = "Repository.queryNodeByEmail"

	logger := logs.FromContext(ctx, operation).With(slog.Time("start", time.Now()))

	row := r.db.QueryRowContext(ctx, selectNodeByEmail, email, networkID)

	var result node

	result.email.String = email

	if err := row.Scan(&result.id, &result.publicIP, &result.updatedAt, &result.localIPString); err != nil {
		logger.Error("query selectNodeByEmail failed", slog.String("query", selectNodeByEmail), slog.String("err", err.Error()))

		return node{}, fmt.Errorf("%s: %w", operation, err)
	}

	return result, nil
}

func (r *Repository) queryByUpdatedAt(ctx context.Context, updateAt int, email string, networkID int) ([]node, error) {
	const operation = "Repository.queryByUpdatedAt"

	logger := logs.FromContext(ctx, operation).With(slog.Time("start", time.Now()))

	stmt, err := r.db.PrepareContext(ctx, selectNodeByUpdated)
	if err != nil {
		logger.Error("preparation for query failed", slog.String("query", selectNodeByEmail), slog.String("err", err.Error()))

		return nil, fmt.Errorf("%s: %w", operation, err)
	}

	rows, err := stmt.QueryContext(ctx, updateAt, email, networkID)
	if err != nil {
		logger.Error("query selectNodeByUpdated failed", slog.String("query", selectNodeByUpdated), slog.String("err", err.Error()))

		return nil, fmt.Errorf("%s: %w", operation, err)
	}

	result := []node{}

	for rows.Next() {
		var r node

		if err := rows.Scan(&r.id, &r.email, &r.publicIP, &r.updatedAt, &r.localIPString); err != nil {
			logger.Error("scan queryByUpdatedAt failed", slog.String("query", selectNodeByUpdated), slog.String("err", err.Error()))

			return nil, fmt.Errorf("%s: %w", operation, err)
		}

		result = append(result, r)
	}

	if err := rows.Err(); err != nil {
		logger.Error("read query from selectNodeByUpdated failed", slog.String("query", selectNodeByUpdated), slog.String("err", err.Error()))

		return nil, fmt.Errorf("%s: %w", operation, err)
	}

	logger.Debug("deu bom", slog.Any("res", result))

	return result, nil
}
