FROM node

COPY . /home/paris-velib/frontend

# Using a non-privileged user:
WORKDIR /home/paris-velib/frontend

RUN [ "npm", "install" ]
CMD [ "npm", "start" ]

EXPOSE 80

