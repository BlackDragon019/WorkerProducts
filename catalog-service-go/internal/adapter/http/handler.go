package http

import (
	"catalog-service/internal/domain"
	"catalog-service/internal/usecase"
	"encoding/json"
	"net/http"

	"github.com/gorilla/mux"
)

type Handler struct {
	UC *usecase.ProductUseCase
}

// Buscamos por id
func (h *Handler) GetByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id := vars["id"]

	product, err := h.UC.ExecuteGetById(id)
	if err != nil {
		http.Error(w, "Producto no encontrado", http.StatusNotFound)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(product)
}

// Creamos un producto
func (h *Handler) Create(w http.ResponseWriter, r *http.Request) {
	var p domain.Product

	if err := json.NewDecoder(r.Body).Decode(&p); err != nil {
		http.Error(w, "Datos inválida", http.StatusBadRequest)
		return
	}

	if err := h.UC.ExecuteCreate(&p); err != nil {
		http.Error(w, err.Error(), http.StatusUnprocessableEntity)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(p)
}

// Actualizamos un producto
func (h *Handler) Update(w http.ResponseWriter, r *http.Request) {
	id := mux.Vars(r)["id"]
	var p domain.Product
	json.NewDecoder(r.Body).Decode(&p)
	h.UC.ExecuteUpdate(id, &p)
	w.WriteHeader(http.StatusOK)
}

// Eliminamos un producto
func (h *Handler) Delete(w http.ResponseWriter, r *http.Request) {
	id := mux.Vars(r)["id"]
	h.UC.ExecuteDelete(id)
	w.WriteHeader(http.StatusNoContent)
}

func (h *Handler) GetAll(w http.ResponseWriter, r *http.Request) {
	products, err := h.UC.ExecuteGetAll()
	if err != nil {
		http.Error(w, "error al obtener productos", http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(products)
}
