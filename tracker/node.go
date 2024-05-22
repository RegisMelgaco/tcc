package tracker

import "time"

type Node struct {
	Email     string
	PublicIP  string
	LocalIPs  []string
	UpdatedAt time.Time
}
