const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const reasonSchema = new Schema({
    classId: {
        type: Schema.Types.ObjectId,
        ref: 'Class'
    },
    tutorId: {
        type: Schema.Types.ObjectId,
        ref: 'Tutor'
    },
    reason: {
        type: String,
        required: true
    }
});

const Reason = mongoose.model('Reason', reasonSchema);
module.exports = Reason;