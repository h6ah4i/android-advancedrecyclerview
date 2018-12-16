FROM squidfunk/mkdocs-material

RUN pip install mkdocs-markdownextradata-plugin

COPY docker-entrypoint.sh /bin/docker-entrypoint.sh
RUN chmod +x /bin/docker-entrypoint.sh
ENTRYPOINT ["/bin/docker-entrypoint.sh", "mkdocs"]

