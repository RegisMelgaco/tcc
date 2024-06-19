package repository

import (
	"database/sql"
	"fmt"
	"strings"

	_ "github.com/mattn/go-sqlite3"
)

type Repository struct {
	db *sql.DB
}

type node struct {
	email         sql.NullString
	publicIP      sql.NullString
	localIPString sql.NullString
	updatedAt     sql.NullTime
	id            sql.NullInt64
}

func (n node) localIPs() []string {
	if n.localIPString.String == "" {
		return []string{}
	}

	return strings.Split(n.localIPString.String, ",")
}

func New() (*Repository, error) {
	db, err := sql.Open("sqlite3", "tracke_db.sqlite")
	if err != nil {
		return nil, fmt.Errorf("create repository: %w", err)
	}

	_, err = db.Exec(schema)
	if err != nil {
		return nil, fmt.Errorf("failed to run migration: %w", err)
	}

	return &Repository{db: db}, nil
}

func (r *Repository) Close() error {
	err := r.db.Close()
	if err != nil {
		return fmt.Errorf("failed to close db: %w", err)
	}

	return nil
}
