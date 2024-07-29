package tracker

type Node struct {
	Email     string
	PublicIP  string
	LocalIPs  []string
	UpdatedAt int
}
