package main

import (
	"local/tracker/handler"
	"local/tracker/logs"
	"local/tracker/repository"
	"log/slog"
	"net/http"
)

func main() {
	logs.EnableDebug()

	repo, err := repository.New()
	if err != nil {
		slog.Error("failed to create repository", slog.String("err", err.Error()))
	}

	defer repo.Close()

	h := handler.New(handler.Config{Debug: true}, repo)

	slog.Info("start listening", slog.String("addrs", ":3000"))

	http.ListenAndServe(":3000", h)
}
