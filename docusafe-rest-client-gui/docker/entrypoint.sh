#!/bin/sh -e

DIST_FOLDER=/opt/app-root/src

if [ -f ${DIST_FOLDER}/main.js ]; then
  sed -i -e \
    's#___SB_BACKEND_URL___#'"$BACKEND_URL"'#g' \
    ${DIST_FOLDER}/main.js
else
  sed -i -e \
    's#___SB_BACKEND_URL___#'"$BACKEND_URL"'#g' \
    ${DIST_FOLDER}/main.*.js
fi

exec "$@"
