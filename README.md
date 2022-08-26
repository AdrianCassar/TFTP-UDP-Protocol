<div align="center">

# TFTP UDP Protocol

</div>

The "Trivial File Transfer Protocol" (RFC 1350) is a simple, lockstep, file transfer protocol that allows a client to get or put a file onto a remote host.

This protocol was developed following the [TFTP RFC](https://www.ietf.org/rfc/rfc1350.txt.pdf) specification.

## Showcase

https://user-images.githubusercontent.com/78108584/186881277-ef89b320-f333-43f2-a608-48ae88a160d8.mp4

## Arguments

Default IP address is 127.0.0.1 (loopback).

Default port is 10000.

### Server Arguments

```
java -jar TFTP-UDP-Server.jar

java -jar TFTP-UDP-Server.jar ServerPort
```

### Client Arguments

```
java -jar TFTP-UDP-Client.jar

java -jar TFTP-UDP-Client.jar ServerIP ServerPort
```

## TFTP Operation Diagrams

<div align="center">

<img title="TFTP Read Operation" src="http://www.tcpipguide.com/free/diagrams/tftpread.png" width="500" height="400">

<img title="TFTP Write Operation" src="http://www.tcpipguide.com/free/diagrams/tftpwrite.png" width="500" height="400">

</div>

