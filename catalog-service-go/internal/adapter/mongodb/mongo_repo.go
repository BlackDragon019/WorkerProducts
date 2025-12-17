package mongodb

import (
	"catalog-service/internal/domain"
	"context"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
)

type MongoRepo struct {
	collection *mongo.Collection
}

func NewMongoRepo(client *mongo.Client) *MongoRepo {
	return &MongoRepo{
		collection: client.Database("catalog_db").Collection("products"),
	}
}

func (r *MongoRepo) Create(p *domain.Product) error {
	_, err := r.collection.InsertOne(context.TODO(), p)
	return err
}

func (r *MongoRepo) GetByID(id string) (*domain.Product, error) {
	var p domain.Product
	err := r.collection.FindOne(context.TODO(), bson.M{"productId": id}).Decode(&p)
	return &p, err
}

func (r *MongoRepo) Update(id string, p *domain.Product) error {
	filtro := bson.M{"productId": id}
	actualizacion := bson.M{"$set": p}
	_, err := r.collection.UpdateOne(context.TODO(), filtro, actualizacion)
	return err
}

func (r *MongoRepo) Delete(id string) error {
	_, err := r.collection.DeleteOne(context.TODO(), bson.M{"productId": id})
	return err
}

func (r *MongoRepo) GetAll() ([]*domain.Product, error) {
	cursor, err := r.collection.Find(context.TODO(), bson.D{})
	if err != nil {
		return nil, err
	}
	defer cursor.Close(context.TODO())

	var products []*domain.Product

	if err := cursor.All(context.TODO(), &products); err != nil {
		return nil, err
	}

	return products, nil
}
