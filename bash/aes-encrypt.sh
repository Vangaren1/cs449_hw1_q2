#!/bin/bash

function help()
{
	echo "aes-encrypt -in plaintext -out ciphertext -sec secure_channel"
}

function getPass()
{
	while true; do
		read -s -p "Password : " password
		echo 
		read -s -p "Confirm Password : " password2
		echo
		[[ "$password" = "$password2" && ${#password} -gt 3 ]] && break
		echo "Please try again, passwords did not match or was shorter than 4 characters"
	done
}

if [[ $1 != "-in" || $3 != "-out" || $5 != "-sec" || $# -ne "6" ]];
then
	help
else
	getPass
	openssl enc -aes-128-gcm -nosalt -in $2 -out $4 -pass pass:$password -p > $6
fi
