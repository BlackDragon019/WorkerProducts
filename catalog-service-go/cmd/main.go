package main

import (
	apiHttp "catalog-service/internal/adapter/http"
	"catalog-service/internal/adapter/mongodb"
	"catalog-service/internal/usecase"
	"context"
	"log"
	"net/http"
	"os"

	"github.com/gorilla/mux"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

func main() {
	// Configuramos la conexión a MongoDB
	uri := os.Getenv("MONGO_URI")
	if uri == "" {
		uri = "mongodb://localhost:27017"
	}
	client, _ := mongo.Connect(context.TODO(), options.Client().ApplyURI(uri))

	repo := mongodb.NewMongoRepo(client)
	uc := &usecase.ProductUseCase{Repo: repo}
	h := &apiHttp.Handler{UC: uc}

	r := mux.NewRouter()

	r.HandleFunc("/api/v1/products", h.GetAll).Methods("GET")
	r.HandleFunc("/api/v1/products", h.Create).Methods("POST")
	r.HandleFunc("/api/v1/products/{id}", h.GetByID).Methods("GET")
	r.HandleFunc("/api/v1/products/{id}", h.Update).Methods("PUT")
	r.HandleFunc("/api/v1/products/{id}", h.Delete).Methods("DELETE")

	// Customers (simple endpoint used by worker for enrichment/validation)
	r.HandleFunc("/api/v1/customers/{id}", h.GetCustomerByID).Methods("GET")

	log.Println("Servidor escuchando en el puerto 8081")
	http.ListenAndServe(":8081", r)
}
