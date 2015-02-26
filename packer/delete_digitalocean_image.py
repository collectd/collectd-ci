#!/usr/bin/env python

import os
import sys
import requests

#import logging
#logging.basicConfig(level=logging.DEBUG)

if len(sys.argv) != 2:
    print "Usage: %s <image name>" % sys.argv[0]
    exit(1)
else:
    image = sys.argv.pop()

base_url = 'https://api.digitalocean.com/v2/images'
api_token = os.environ['DIGITALOCEAN_API_TOKEN']

auth = requests.auth.HTTPBasicAuth(api_token, '')
headers = {'content-type': 'application/json'}

response = requests.get("%s?page=1&per_page=100&private=true" % base_url,
    headers=headers, auth=auth)

data = response.json()

for i in data['images']:
    if i['name'] == image:
        print requests.delete("%s/%s" % (base_url, i['id']),
            headers=headers, auth=auth)

