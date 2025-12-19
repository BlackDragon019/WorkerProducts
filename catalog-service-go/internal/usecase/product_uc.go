package usecase

import (
	"catalog-service/internal/domain"
	"errors"
)

type ProductUseCase struct {
	Repo domain.ProductRepository
}

func (uc *ProductUseCase) ExecuteUpdate(id string, p *domain.Product) error {
	// Ensure the productId is preserved even if not present in the payload
	if p.ID == "" {
		p.ID = id
	}
	if p.Price <= 0 {
		return nil
	}
	return uc.Repo.Update(id, p)
}

func (uc *ProductUseCase) ExecuteDelete(id string) error {
	return uc.Repo.Delete(id)
}

func (uc *ProductUseCase) ExecuteCreate(p *domain.Product) error {
	if p.Name == "" {
		return errors.New("el nombre del producto es obligatorio")
	}
	if p.Price <= 0 {
		return errors.New("el precio debe ser mayor a cero")
	}

	return uc.Repo.Create(p)
}

func (uc *ProductUseCase) ExecuteGetById(id string) (*domain.Product, error) {
	if id == "" {
		return nil, errors.New("se requiere un id de producto válido")
	}

	product, err := uc.Repo.GetByID(id)
	if err != nil {
		return nil, errors.New("no encontramos el producto solicitado")
	}
	return product, nil
}

func (uc *ProductUseCase) ExecuteGetAll() ([]*domain.Product, error) {
	return uc.Repo.GetAll()
}
