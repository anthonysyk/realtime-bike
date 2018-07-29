export default function ({store}) {
  const socket = new WebSocket('ws://localhost:9000');

  socket.addEventListener('open', function () {
    socket.send('Hello Server!');
  });

  socket.addEventListener('message', function (event) {
    console.log('Message from server ', event.data);
    store.commit('carte/updateStation', event.data)
  });
}
