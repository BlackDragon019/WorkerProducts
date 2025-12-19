package domain

// Product
type Product struct {
	ID    string  `json:"productId" bson:"productId"`
	Name  string  `json:"name" bson:"name"`
	Price float64 `json:"price" bson:"price"`
}

// Customer
type Customer struct {
	ID     string `json:"customerId" bson:"customerId"`
	Name   string `json:"name" bson:"name"`
	Active bool   `json:"active" bson:"active"`
}
