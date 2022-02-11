# ws.neko

A simple proxy software made for Telegram. Works well for areas that have MTProxy or Socks5 blocked.

## Usage

### Android

**[Nekogram](https://nekogram.app)** has built-in support for **ws.neko**. Or you can try the [Other](#Other) part with [Termux](https://termux.com/).

### Other

Install [Java](https://java.com/) 8+, then run the following command in terminal.
```shell
java -jar ./ws.neko.jar
```

## How it works?

```
Clients <-> ws.neko <-> Cloudflare <-> Telegram
▴ On Your Device ▴
```
