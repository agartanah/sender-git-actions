FROM alpine:latest

RUN apk add --no-cache curl bash

COPY notify.sh /notify.sh

RUN chmod +x /notify.sh

ENTRYPOINT ["/notify.sh"]