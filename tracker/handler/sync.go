package handler

import (
	"encoding/json"
	"local/tracker"
	"local/tracker/logs"
	"log/slog"
	"net/http"
	"strings"
	"time"
)

type SyncOutput struct {
	Self      NodeData   `json:"self"`
	Neighbors []NodeData `json:"neighbors"`
}

type NodeData struct {
	Email     string    `json:"email"`
	PublicIP  string    `json:"public_ip"`
	LocalIPs  []string  `json:"local_ips"`
	UpdatedAt time.Time `json:"updated_at"`
}

func (h Handler) Sync(w http.ResponseWriter, r *http.Request) {
	const operation = "Handler.Sync"

	logger := logs.FromContext(r.Context(), operation).With(slog.Time("start", time.Now()))

	var input NodeData
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(map[string]string{"msg": "invalid json"})

		logger.Warn("bad request", slog.String("err", err.Error()))

		return
	}

	input.PublicIP = strings.Split(r.RemoteAddr, ":")[0]

	newNodes, err := h.repo.Sync(r.Context(), tracker.Node{
		Email:     input.Email,
		PublicIP:  input.PublicIP,
		LocalIPs:  input.LocalIPs,
		UpdatedAt: input.UpdatedAt,
	})
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		json.NewEncoder(w).Encode(map[string]string{"msg": "unexpected error"})

		logger.Error("intenal server error", slog.String("err", err.Error()))

		return
	}

	var output SyncOutput

	output.Self = input

	var rs []NodeData
	for _, n := range newNodes {
		rs = append(rs, NodeData{
			Email:     n.Email,
			PublicIP:  n.PublicIP,
			LocalIPs:  n.LocalIPs,
			UpdatedAt: n.UpdatedAt,
		})
	}

	output.Neighbors = rs

	if err = json.NewEncoder(w).Encode(rs); err != nil {
		logger.Error("failed to write response", slog.String("err", err.Error()))
	}
}
