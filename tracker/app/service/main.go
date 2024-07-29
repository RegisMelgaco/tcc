package main

import (
	"local/tracker/handler"
	"local/tracker/logs"
	"local/tracker/repository"
	"log/slog"
	"net/http"
	"os"

	_ "github.com/joho/godotenv/autoload"
)

func main() {
	logs.EnableDebug()

	repo, err := repository.New()
	if err != nil {
		slog.Error("failed to create repository", slog.String("err", err.Error()))

		return
	}

	defer repo.Close()

	h := handler.New(handler.Config{Debug: true}, repo)

	addrs := os.Getenv("ADDRS")
	if addrs == "" {
		addrs = "0.0.0.0:3000"
	}

	slog.Info("start listening", slog.String("addrs", addrs))

	http.ListenAndServe(addrs, h)
}
