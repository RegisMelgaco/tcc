package logs

import (
	"context"
	"log/slog"
	"os"
)

var handler slog.Handler

func init() {
	handler = slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{
		Level: slog.LevelInfo,
	})

	slog.SetDefault(slog.New(handler))
}

func New() *slog.Logger {
	return slog.New(handler)
}

func EnableDebug() {
	handler = slog.NewTextHandler(os.Stdout, &slog.HandlerOptions{
		Level: slog.LevelDebug,
	})

	slog.SetDefault(slog.New(handler))
}

type loggerKey struct{}

func ToContext(ctx context.Context, logger *slog.Logger) context.Context {
	return context.WithValue(ctx, loggerKey{}, logger)
}

func FromContext(ctx context.Context, operation string) *slog.Logger {
	logger, ok := ctx.Value(loggerKey{}).(*slog.Logger)
	if !ok || logger == nil {
		return slog.Default()
	}

	return logger.With(slog.String("operation", operation))
}
