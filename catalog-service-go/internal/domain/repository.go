package domain

type ProductRepository interface {
	GetByID(id string) (*Product, error)
	Create(*Product) error
	Update(id string, p *Product) error
	Delete(id string) error
	GetAll() ([]*Product, error)
}
