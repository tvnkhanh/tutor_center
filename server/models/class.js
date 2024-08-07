const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const classSchema = new Schema({
    studentInfo: {
        type: String,
    },
    schedule: [
        {
            type: String,
            required: true,
        }
    ],
    status: {
        type: String,
        required: true,
        enum: ['assigned', 'approved', 'pending', 'rejected', 'completed'],
        default: 'pending'
    },
    salary: {
        type: String,
        required: true,
    },
    tutorId: [
        { 
            type: Schema.Types.ObjectId, 
            ref: 'Tutor' 
        }
    ],
    clientId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Client' 
    },
    form: {
        type: String,
        required: true
    },
    requirement: {
        type: String
    },
    address: {
        type: String,
        required: true
    },
    subjects: [
        {
            type: Schema.Types.ObjectId,
            ref: 'Subject'
        }
    ],
    updateDate: {
        type: Date,
        default: Date.now
    }
})

const Class = mongoose.model('Class', classSchema);
module.exports = Class;