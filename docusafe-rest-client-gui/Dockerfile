FROM centos/nginx-112-centos7

CMD ["/usr/libexec/s2i/run"]

COPY ./dist /opt/app-root/src
COPY ./docker/nginx.conf /opt/app-root/etc/nginx.default.d/default.conf
COPY ./docker/entrypoint.sh /opt/entrypoint.sh

USER root
RUN chgrp -R 0 /opt/app-root/src && \
    chmod -R g=u /opt/app-root/src
USER 1001
