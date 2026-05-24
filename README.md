# Simple-DNS-Filter

This is a small project I made for my home network that works as an ad, cookie, and tracker blocker using DNS filtering. It requires a blocklist entitled "blocklist.txt" that it pulls the domains to block from. I've been using the light list from Hagezi's similar DNS filtering blocker, found here: https://github.com/hagezi/dns-blocklists#light. Most operating systems already have some DNS system running on port 53, so you may have to disable that for this to work on the local version. This will be fixed in later commits. Additionally, using port 53 will require sudo access on MacOS/linux and administrator access on Windows. All queries are printed to a log.txt file as well.

For now, all that works is the local version, but I'm currently working on a network-wide version.
