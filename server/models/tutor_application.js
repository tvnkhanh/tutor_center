const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const TutorApplicationSchema = new Schema({
    tutorId: String,
    applicationDate: Date,
    applicationStatus: String,
});

const TutorApplication = mongoose.model('TutorApplication', TutorApplicationSchema);
module.exports = TutorApplication;
