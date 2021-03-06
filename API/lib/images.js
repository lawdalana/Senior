// Copyright 2015-2016, Google, Inc.
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

'use strict';

const Storage = require('@google-cloud/storage');
const config = require('../config');

const CLOUD_BUCKET = config.get('CLOUD_BUCKET');
const CLOUD_BUCKET_USER_PROFILE_PIC = "user_profile_list";
const CLOUD_BUCKET_REST_PROFILE_PIC = "resteraunt_profile_list";

const storage = Storage({
  projectId: config.get('GCLOUD_PROJECT')
});
const bucket = storage.bucket(CLOUD_BUCKET);
const bucket_user_pro = storage.bucket(CLOUD_BUCKET_USER_PROFILE_PIC);
const bucket_rest_pro = storage.bucket(CLOUD_BUCKET_REST_PROFILE_PIC);


// Returns the public, anonymously accessable URL to a given Cloud Storage
// object.
// The object's ACL has to be set to public read.
// [START public_url]
function getPublicUrl (filename) {
  return `https://storage.googleapis.com/${CLOUD_BUCKET}/${filename}`;
}

function getUserProfileUrl (filename)
{
	return `https://storage.googleapis.com/${CLOUD_BUCKET_USER_PROFILE_PIC}/${filename}`;
}

function getRestProfileUrl(filename)
{
	return `https://storage.googleapis.com/${CLOUD_BUCKET_REST_PROFILE_PIC}/${filename}`;
}
// [END public_url]

// Express middleware that will automatically pass uploads to Cloud Storage.
// req.file is processed and will have two new properties:
// * ``cloudStorageObject`` the object name in cloud storage.
// * ``cloudStoragePublicUrl`` the public url to the object.
// [START process]
function sendUploadToGCS (req, res, next) {
	
	var data = req.body;
	console.log(data);
	
  if (!(req.file || req.files)) {
    return next();
  }

  var tmp = req.files;
  //console.log(tmp);
  
  tmp.forEach(function (item)
  {
    var x = item.originalname;
	const gcsname = Date.now() + x;
	const file = bucket.file(gcsname);
	
	item.cloudStorageObject = gcsname;
	item.cloudStoragePublicUrl = getPublicUrl(gcsname);
	
    const stream = file.createWriteStream({
		metadata: {
		  contentType: item.mimetype
		}
	});

	stream.on('error', (err) => {
		item.cloudStorageError = err;
		next(err);
	});

	stream.on('finish', () => {
		next();
	});
	
	stream.end(item.buffer);
	
	console.log("\n"+item.cloudStoragePublicUrl+ " : done");
	
  });	
	
}

function sendUploadToGCS_UserProfile(req, res, next)
{
	var data = req.body;
	console.log(data);
	
	if (!(req.file || req.files)) {
    return next();
  }
 
 var tmp = req.files;
  //console.log(tmp);
  
  tmp.forEach(function (item)
  {
    var x = item.originalname;
	const gcsname = Date.now() + x;
	const file = bucket_user_pro.file(gcsname);
	
	item.cloudStorageObject = gcsname;
	item.cloudStoragePublicUrl = getUserProfileUrl(gcsname);
	
    const stream = file.createWriteStream({
		metadata: {
		  contentType: item.mimetype
		}
	});

	stream.on('error', (err) => {
		item.cloudStorageError = err;
		next(err);
	});

	stream.on('finish', () => {
		next();
	});
	
	stream.end(item.buffer);
	
	console.log("\n"+item.cloudStoragePublicUrl+ " : done");
	
  }); 
	
}

function sendUploadToGCS_RestaurantProfile(req, res, next)
{
	var data = req.body;
	console.log(data);
	
	if (!(req.file || req.files)) {
    return next();
  }
 
 var tmp = req.files;
  //console.log(tmp);
  
  tmp.forEach(function (item)
  {
    var x = item.originalname;
	const gcsname = Date.now() + x;
	const file = bucket_rest_pro.file(gcsname);
	
	item.cloudStorageObject = gcsname;
	item.cloudStoragePublicUrl = getRestProfileUrl(gcsname);
	
    const stream = file.createWriteStream({
		metadata: {
		  contentType: item.mimetype
		}
	});

	stream.on('error', (err) => {
		item.cloudStorageError = err;
		next(err);
	});

	stream.on('finish', () => {
		next();
	});
	
	stream.end(item.buffer);
	
	console.log("\n"+item.cloudStoragePublicUrl+ " : done");
	
  }); 
	
}



// [END process]

// Multer handles parsing multipart/form-data requests.
// This instance is configured to store images in memory.
// This makes it straightforward to upload to Cloud Storage.
// [START multer]
const Multer = require('multer');
const multer = Multer({
  storage: Multer.MemoryStorage,
  limits: {
    fileSize: 5 * 1024 * 1024 // no larger than 5mb
  }
});
// [END multer]

module.exports = {
  getPublicUrl,
  sendUploadToGCS,
  multer,
  sendUploadToGCS_UserProfile,
  sendUploadToGCS_RestaurantProfile
};
