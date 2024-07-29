package handler

import (
	"bytes"
	"context"
	"io"
	"local/tracker/logs"
	"log/slog"
	"net/http"
	"time"
)

func withTimeout(timout time.Duration, next http.HandlerFunc) http.HandlerFunc {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ctx, cancel := context.WithTimeout(r.Context(), timout)
		defer cancel()

		next.ServeHTTP(w, r.WithContext(ctx))
	})
}

func withLogs(next http.HandlerFunc) http.HandlerFunc {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		start := time.Now()

		logger := logs.New().
			With(
				slog.Time("start", start),
				slog.String("path", r.URL.Path),
				slog.String("method", r.Method),
				slog.String("user_agent", r.UserAgent()),
			)

		logger.Debug("started handling request")

		ww := &responseWriter{ResponseWriter: w, status: http.StatusOK}

		ctx := logs.ToContext(r.Context(), logger)

		next.ServeHTTP(ww, r.WithContext(ctx))

		logger.Info("request handled", slog.Int("status", ww.status), slog.Duration("duration", time.Since(start)))
	})
}

func withDefaultHeaders(next http.HandlerFunc) http.HandlerFunc {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Add("Content-Type", "application/json")

		next.ServeHTTP(w, r)
	})
}

type responseWriter struct {
	status int
	http.ResponseWriter
}

func (w *responseWriter) WriteHeader(statusCode int) {
	w.status = statusCode
	w.ResponseWriter.WriteHeader(statusCode)
}
