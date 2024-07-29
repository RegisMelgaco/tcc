package handler

import (
	"encoding/json"
	"local/tracker"
	"local/tracker/logs"
	"log/slog"
	"net/http"
	"time"
)

type CreateNetworkInput struct {
	Secret string `json:"secret"`
	Name   string `json:"name"`
}

func (h *Handler) CreateNetwork(w http.ResponseWriter, r *http.Request) {
	const operation = "Repository.insert"

	logger := logs.FromContext(r.Context(), operation).With(slog.Time("start", time.Now()))

	var input CreateNetworkInput
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(map[string]string{"msg": "invalid json"})

		logger.Warn("bad request", slog.String("err", err.Error()))

		return
	}

	err := h.repo.CreateNetwork(r.Context(), tracker.Network{
		Name:   input.Name,
		Secret: input.Secret,
	})
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		json.NewEncoder(w).Encode(map[string]string{"msg": "internal server error"})

		logger.Warn("internal server error", slog.String("err", err.Error()))

		return
	}

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(map[string]string{"msg": "created with success"})
}
