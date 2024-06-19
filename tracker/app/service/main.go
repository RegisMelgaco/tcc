package main

import (
	"local/tracker/handler"
	"local/tracker/logs"
	"local/tracker/repository"
	"log/slog"
	"net/http"
)

const ADDRS = "0.0.0.0:3000"

func main() {
	logs.EnableDebug()

	repo, err := repository.New()
	if err != nil {
		slog.Error("failed to create repository", slog.String("err", err.Error()))

		return
	}

	defer repo.Close()

	h := handler.New(handler.Config{Debug: true}, repo)

	slog.Info("start listening", slog.String("addrs", ADDRS))

	http.ListenAndServe(ADDRS, h)
}
