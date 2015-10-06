# DHTcrawler [![Build Status](https://travis-ci.org/wihoho/DHTcrawler.svg?branch=master)](https://travis-ci.org/wihoho/DHTcrawler)
Crawl torrent through DHT in Java

`
I have no time to continue this project right now. Hope that I am able to resume this project in the near future.
`

## Run
Execute the main method in `Crawler.java`.

You will get sample results in console as below

    Client starts
    Server starts
    1:TÉ£?êï®9Y¿PgÔyôñüÁÖ
    2:FïØOS3ÄëÜ R¶?ÛsÖÛ
    3:`$}@o?ŞGÏ?
    ÏMåW%¸¶
    4:TXnQpKüÎî2?Bi?d?N'b
    5:®c,@Y?Ê)©$a?0ÛC?p

The strange string after each number is the infohash of torrent encoded by ISO-8859-1. You may decode this infohash back to byte array using ISO-8859-1.