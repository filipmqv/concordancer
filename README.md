# Concordancer

Scala app (server + client), functional programming, actors.

Server:
* Listens for requests (word to search, neighbours count, URLs of the books). 
* Once a request comes, server adds actors for new URLs, gives word to search to suitable actors, gathers results into response.
* Concordancer actors are supposed to download the book from URL, obtain plain text from HTML and wait for upcomming search requests.

Client:
* sends requests to the server, prints the result
