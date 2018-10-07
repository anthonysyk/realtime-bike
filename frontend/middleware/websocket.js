export default function({ store }) {
  const socket = new WebSocket("ws://192.168.1.26:9001")

  socket.addEventListener("open", function() {
    socket.send("Hello Server!")
  })

  socket.addEventListener("message", function(event) {
    console.log("Message from server ", event.data)
    console.log(store)
    store.commit("carte/updateStation", event.data)
  })
}
