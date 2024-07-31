const express = require("express");
const Class = require("../models/class");
const auth = require("../middlewares/auth");
const Subject = require("../models/subject");

const classRouter = express.Router();

classRouter.get("/api/classes", auth, async (req, res) => {
  try {
    const classes = await Class.aggregate([
      {
        $lookup: {
          from: 'subjects', 
          localField: 'subjects', 
          foreignField: '_id', 
          as: 'subjectDetails' 
        }
      },
      {
        $unwind: {
          path: '$subjectDetails',
          preserveNullAndEmptyArrays: true 
        }
      },
      {
        $group: {
          _id: '$_id', 
          studentInfo: { $first: '$studentInfo' },
          schedule: { $first: '$schedule' },
          status: { $first: '$status' },
          salary: { $first: '$salary' },
          tutorId: { $first: '$tutorId' },
          clientId: { $first: '$clientId' },
          form: { $first: '$form' },
          requirement: { $first: '$requirement' },
          address: { $first: '$address' },
          subjects: { $first: '$subjects' },
          subjectDetails: { $push: '$subjectDetails' },
          __v: { $first: '$__v' }
        }
      }
    ]);

    res.status(200).json(classes);
  } catch (e) {
    res.status(500).json({ error: "Failed to get classes data" });
  }
});

classRouter.post("/api/create-class", auth, async (req, res) => {
  try {
    const {
      studentInfo,
      schedule,
      status,
      salary,
      clientId,
      tutorId,
      form,
      requirement,
      address,
      subjectIds,
    } = req.body;

    const subjects = await Subject.find({ _id: { $in: subjectIds } });
    if (subjects.length !== subjectIds.length) {
      return res.status(400).json({ message: "Some subjects were not found" });
    }

    const newClass = new Class({
      studentInfo: studentInfo,
      schedule: schedule,
      status: status,
      salary: salary,
      tutorId: tutorId,
      clientId: clientId,
      form: form,
      requirement: requirement,
      address: address,
      subjects: subjectIds
    });

    const savedClass = await newClass.save();
    res.status(201).json({ message: "Tutor created successfully", class: savedClass, subjects });
  } catch (e) {
    res.status(500).json({ error: "Failed to create class" });
  }
});

module.exports = classRouter;
