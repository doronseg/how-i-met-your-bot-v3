[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/X8X7E2ECO)

# how i met your bot v3

This repository is for how i met your bot from [how i met your discord](https://discord.gg/himym).

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

### Initializing Local Project

To initialize, just clone the repository, In the main folder, which would be named how i met your discord if you cloned
it, you need to create 2 files, `db.db`, and `.env`, you will also need the mp4 file, in order for the automatic reply
feature to work.

You will also need to create your own bot, on the [Discord Developer Portal](https://discord.com/developers), and copy
the token.

### .env file

```
TOKEN=YOUR TOKEN GOES HERE

DB=jdbc:sqlite:db.db
```

### Database

As of right now, we have 1 SQLITE databases, if you want to contribute and make an update that includes a new database,
make sure to create an issue first.

```
CREATE TABLE "" (
	"uid"	TEXT NOT NULL UNIQUE,
	"amount"	INTEGER,
	PRIMARY KEY("uid")
);

```

## License

[MIT](https://choosealicense.com/licenses/mit/)
