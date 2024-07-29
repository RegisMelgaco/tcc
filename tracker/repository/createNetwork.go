package repository

import (
	"context"
	"fmt"
	"local/tracker"
	"local/tracker/logs"
	"log/slog"
	"time"
)

func (r *Repository) CreateNetwork(ctx context.Context, input tracker.Network) error {
	const operation = "Repository.CreateNetwork"

	logger := logs.FromContext(ctx, operation).With(slog.Time("start", time.Now()))

	stmt, err := r.db.PrepareContext(ctx, insertNetwork)
	if err != nil {
		err = fmt.Errorf("%s: %w", operation, err)
		logger.Error("failed to prepare statement", slog.String("err", err.Error()))

		return err
	}

	_, err = stmt.ExecContext(ctx, input.Secret, input.Name)
	if err != nil {
		return fmt.Errorf("%s: %w", operation, err)
	}

	return nil
}
