# Kubernetes Cluster
For testing purpose, we have used Docker Desktop integrated K8s cluster

# Deployment
`
kubectl apply -f elastic_deployment.yml
kubectl apply -f elastic_service.yml
`

`
kubectl apply -f backend_deployment.yml
kubectl apply -f backend_service.yml
`

`
kubectl apply -f frontend_deployment.yml
kubectl apply -f frontend_service.yml
`

# Ingress
To allow communication outside the K8s cluster, we have decided to use ingress instead of forwarding the request manually

`
kubectl apply -f elastic_ingress.yml
kubectl apply -f backend_ingress.yml
kubectl apply -f frontend_ingress.yml
`

# APP Structure

## Backend
- SpringBoot application that calls a free API from Alpaca to ingest data about energy stock quotations.
- It gets daily updates via a cron job which runs at midnight

## Frontend
- Queries against the backend and creates candle sticks graphics

## Database
- Elasticsearch

For all compoments we have generated a docker image which is already uploaded on docker hub.