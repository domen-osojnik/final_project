var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var recordingsSchema = new Schema({
	'date' : Date,
	'events': [{
		type: Schema.Types.ObjectId,
		ref: 'events'
	}],
	'images': [{
		type: Schema.Types.ObjectId,
		ref: 'image'
	}],
});

module.exports = mongoose.model('recordings', recordingsSchema);
