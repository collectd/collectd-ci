#!/usr/bin/env python

import sys
import xml.etree.ElementTree as ET
import json
import requests

def digitalocean_snapshots(token):
    base_url = 'https://api.digitalocean.com/v2/images'

    auth = requests.auth.HTTPBasicAuth(token, '')
    headers = {'content-type': 'application/json'}
    response = requests.get("%s?page=1&per_page=100&private=true" % base_url,
            headers=headers, auth=auth)

    return response.json()


def slave_template(image):
    label = image['name']

    slave = ET.fromstring('''
<com.dubture.jenkins.digitalocean.SlaveTemplate>
  <imageId />
  <labelString />
  <labels />
  <idleTerminationInMinutes>50</idleTerminationInMinutes>
  <numExecutors>3</numExecutors>
  <sizeId>512mb</sizeId>
  <regionId>fra1</regionId>
  <userData></userData>
  <initScript></initScript>
</com.dubture.jenkins.digitalocean.SlaveTemplate>
''')

    slave.find('imageId').text = str(image['id'])
    slave.find('labelString').text = label
    slave.find('labels').text = label

    return slave


def setup_digitalocean_cloud():
    xmlnode = root.find('clouds').find('com.dubture.jenkins.digitalocean.Cloud')

    if 'digitalocean_sshkeyid' in vars:
        xmlnode.find('sshKeyId').text = vars['digitalocean_sshkeyid']

    if 'digitalocean_sshprivatekey' in vars:
        xmlnode.find('privateKey').text = "-----BEGIN RSA PRIVATE KEY-----\n"
        xmlnode.find('privateKey').text += vars['digitalocean_sshprivatekey']
        xmlnode.find('privateKey').text += "\n-----END RSA PRIVATE KEY-----"

    if 'digitalocean_v2_token' in vars:
        xmlnode.find('authToken').text = vars['digitalocean_v2_token']
        snapshots = digitalocean_snapshots(vars['digitalocean_v2_token'])
        templates = xmlnode.find('templates')
        templates.clear()
        for snapshot in snapshots['images']:
            if snapshot['type'] == 'snapshot':
                templates.append(slave_template(snapshot))


def setup_github_authentication():
    auth_strategy = ET.fromstring('''
<authorizationStrategy class="org.jenkinsci.plugins.GithubAuthorizationStrategy">
  <rootACL>
    <organizationNameList class="linked-list" />
    <adminUserNameList class="linked-list">
      <string>mfournier</string>
      <string>octo</string>
    </adminUserNameList>
    <authenticatedUserReadPermission>true</authenticatedUserReadPermission>
    <useRepositoryPermissions>false</useRepositoryPermissions>
    <authenticatedUserCreateJobPermission>false</authenticatedUserCreateJobPermission>
    <allowGithubWebHookPermission>true</allowGithubWebHookPermission>
    <allowCcTrayPermission>true</allowCcTrayPermission>
    <allowAnonymousReadPermission>true</allowAnonymousReadPermission>
  </rootACL>
</authorizationStrategy>
''')
    security_realm = ET.fromstring('''
<securityRealm class="org.jenkinsci.plugins.GithubSecurityRealm">
  <githubWebUri>https://github.com</githubWebUri>
  <githubApiUri>https://api.github.com</githubApiUri>
  <clientID />
  <clientSecret />
</securityRealm>
''')

    root.find('useSecurity').text = 'true'
    root.remove(root.find('authorizationStrategy'))
    root.append(auth_strategy)
    root.remove(root.find('securityRealm'))
    root.append(security_realm)
    root.find('securityRealm').find('clientID').text = vars['github_clientid']
    root.find('securityRealm').find('clientSecret').text = vars['github_clientsecret']


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print "Usage: %s /path/to/config.xml /path/to/ansible/group_vars/environment" % sys.argv[0]
        exit(1)
    else:
        ansible_vars = sys.argv.pop()
        config_xml = sys.argv.pop()

    vars = json.loads(open(ansible_vars).read())

    xml = ET.parse(config_xml)
    root = xml.getroot()

    setup_digitalocean_cloud()
    #TODO: setup_libvirt_cloud()
    if 'github_clientid' in vars:
        setup_github_authentication()

    xml.write(config_xml, encoding='UTF-8', xml_declaration=True)

