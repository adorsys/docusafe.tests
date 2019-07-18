#!/bin/sh

export MINIO_DOCKER_NAME=docusafe-test-minio
export MINIO_ACCESS_KEY=simpleAccessKey
export MINIO_SECRET_KEY=simpleSecretKey
export MINIO_BUCKET=adorsys

docker rm -f "$MINIO_DOCKER_NAME"
docker network rm minio-net

docker network create --subnet=192.168.178.0/16 minio-net

docker run --name $MINIO_DOCKER_NAME \
    -d -p 9000:9000 \
    --mount type=bind,source=/Users/valentyn.berezin/Documents/temp/minio,target=/data \
    --net minio-net --ip 192.168.178.60 \
    -e MINIO_ACCESS_KEY=$MINIO_ACCESS_KEY \
    -e MINIO_SECRET_KEY=$MINIO_SECRET_KEY \
    minio/minio \
    server /data

docker run --rm --net minio-net -e MINIO_BUCKET=$MINIO_BUCKET --entrypoint sh minio/mc -c "\
  while ! nc -z 192.168.178.60 9000; do echo 'Wait minio to startup...' && sleep 1; done; \
  mc config host add myminio http://192.168.178.60:9000 $MINIO_ACCESS_KEY $MINIO_SECRET_KEY && \
  mc rm -r --force myminio/$MINIO_BUCKET || true && \
  mc mb myminio/$MINIO_BUCKET && \
  mc policy download myminio/$MINIO_BUCKET \
"
