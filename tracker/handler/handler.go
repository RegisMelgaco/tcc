package handler

import (
	"local/tracker/repository"
	"net/http"
	"time"
)

type Handler struct {
	repo *repository.Repository
}

func New(cfg Config, repo *repository.Repository) http.Handler {
	mux := http.NewServeMux()

	h := Handler{repo: repo}

	healthcheck := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {})
	mux.HandleFunc("GET /healthcheck", withTimeout(5*time.Second, healthcheck))

	mux.HandleFunc("PUT /v1/sync", withDefaultHeaders(withLogs(withTimeout(time.Minute, h.Sync))))

	return mux
}
