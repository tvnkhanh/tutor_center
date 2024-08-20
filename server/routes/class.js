const express = require("express");
const mongoose = require("mongoose");
const Class = require("../models/class");
const auth = require("../middlewares/auth");
const admin = require("../middlewares/admin");
const Subject = require("../models/subject");
const Reason = require("../models/reason");

const classRouter = express.Router();

classRouter.get("/api/classes", auth, async (req, res) => {
  try {
    const classes = await Class.aggregate([
      {
        $lookup: {
          from: "subjects",
          localField: "subjects",
          foreignField: "_id",
          as: "subjectDetails",
        },
      },
      {
        $unwind: {
          path: "$subjectDetails",
          preserveNullAndEmptyArrays: true,
        },
      },
      {
        $group: {
          _id: "$_id",
          studentInfo: { $first: "$studentInfo" },
          schedule: { $first: "$schedule" },
          status: { $first: "$status" },
          salary: { $first: "$salary" },
          tutorId: { $first: "$tutorId" },
          clientId: { $first: "$clientId" },
          form: { $first: "$form" },
          requirement: { $first: "$requirement" },
          address: { $first: "$address" },
          subjects: { $first: "$subjects" },
          subjectDetails: { $push: "$subjectDetails" },
          updateDate: { $first: "$updateDate" }, 
          __v: { $first: "$__v" },
        },
      },
    ]);

    res.status(200).json(classes);
  } catch (e) {
    res.status(500).json({ error: "Failed to get classes data" });
  }
});

classRouter.get("/api/classes/:id", auth, async (req, res) => {
  try {
    const id = req.params.id;

    const classes = await Class.aggregate([
      {
        $match: {
          $or: [
            { tutorId: new mongoose.Types.ObjectId(id) },
            { clientId: new mongoose.Types.ObjectId(id) },
          ],
        },
      },
      {
        $lookup: {
          from: "subjects",
          localField: "subjects",
          foreignField: "_id",
          as: "subjectDetails",
        },
      },
      {
        $unwind: {
          path: "$subjectDetails",
          preserveNullAndEmptyArrays: true,
        },
      },
      {
        $group: {
          _id: "$_id",
          studentInfo: { $first: "$studentInfo" },
          schedule: { $first: "$schedule" },
          status: { $first: "$status" },
          salary: { $first: "$salary" },
          tutorId: { $first: "$tutorId" },
          clientId: { $first: "$clientId" },
          form: { $first: "$form" },
          requirement: { $first: "$requirement" },
          address: { $first: "$address" },
          subjects: { $first: "$subjects" },
          subjectDetails: { $push: "$subjectDetails" },
          updateDate: { $first: "$updateDate" }, // Thêm trường updateDate
          __v: { $first: "$__v" },
        },
      },
    ]);

    res.status(200).json(classes);
  } catch (e) {
    console.log(e);
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
      subjects: subjectIds,
      updateDate: new Date(),
    });

    const savedClass = await newClass.save();

    const classWithDetails = await Class.aggregate([
      {
        $match: { _id: new mongoose.Types.ObjectId(savedClass._id) },
      },
      {
        $lookup: {
          from: "subjects",
          localField: "subjects",
          foreignField: "_id",
          as: "subjectDetails",
        },
      },
      {
        $unwind: {
          path: "$subjectDetails",
          preserveNullAndEmptyArrays: true,
        },
      },
      {
        $group: {
          _id: "$_id",
          studentInfo: { $first: "$studentInfo" },
          schedule: { $first: "$schedule" },
          status: { $first: "$status" },
          salary: { $first: "$salary" },
          tutorId: { $first: "$tutorId" },
          clientId: { $first: "$clientId" },
          form: { $first: "$form" },
          requirement: { $first: "$requirement" },
          address: { $first: "$address" },
          subjects: { $first: "$subjects" },
          subjectDetails: { $push: "$subjectDetails" },
          __v: { $first: "$__v" },
        },
      },
    ]);

    res.status(201).json(classWithDetails[0]);
  } catch (e) {
    console.log(e.message);
    res.status(500).json({ error: "Failed to create class" });
  }
});

classRouter.put("/api/:id/tutor-register/:tutorId", auth, async (req, res) => {
  const classId = req.params.id;
  const tutorId = req.params.tutorId;

  if (!mongoose.Types.ObjectId.isValid(tutorId)) {
    return res.status(400).json({ message: "Invalid tutorId format" });
  }

  try {
    const classData = await Class.findById(classId);

    if (!classData) {
      return res.status(404).json({ message: "Class not found" });
    }

    if (classData.tutorId.includes(tutorId)) {
      return res.status(400).json({ message: "Tutor already registered!" });
    }

    classData.tutorId.push(tutorId);
    classData.updateDate = new Date(); 

    await classData.save();

    res.status(200).json({ message: "Tutor assigned successfully", classData });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: "Server error" });
  }
});

classRouter.get("/api/search-class", auth, async (req, res) => {
  try {
    const { q } = req.query;
    const regex = new RegExp(q, "i");

    const matchedSubjects = await Subject.find({
      subjectName: { $regex: regex },
    });

    const subjectIds = matchedSubjects.map((subject) => subject._id);

    const classes = await Class.aggregate([
      {
        $match: {
          subjects: { $in: subjectIds },
        },
      },
      {
        $lookup: {
          from: "subjects",
          localField: "subjects",
          foreignField: "_id",
          as: "subjectDetails",
        },
      },
    ]);

    res.status(200).json(classes);
  } catch (error) {
    res.status(500).json({ message: "Internal server error" });
  }
});

classRouter.post('/api/create-reason', async (req, res) => {
  const { classId, tutorId, reason } = req.body;

  try {
      const newReason = new Reason({
          classId,
          tutorId,
          reason
      });

      const savedReason = await newReason.save();
      res.status(201).json(savedReason);
  } catch (err) {
      res.status(500).json({ message: err.message });
  }
});

classRouter.delete('/api/delete-reason', async (req, res) => {
  const { classId, tutorId } = req.body;

  try {
      const query = {};
      if (classId) query.classId = classId;
      if (tutorId) query.tutorId = tutorId;

      const deletedReason = await Reason.findOneAndDelete(query);

      if (!deletedReason) {
          return res.status(404).json({ message: 'Reason not found' });
      }

      res.json({ message: 'Reason deleted successfully' });
  } catch (err) {
      res.status(500).json({ message: err.message });
  }
});

classRouter.get('/api/get-reason', async (req, res) => {
  const { classId, tutorId } = req.query;

  try {
      let filter = {};
      if (classId) filter.classId = classId;
      if (tutorId) filter.tutorId = tutorId;

      const reasons = await Reason.find(filter);

      if (reasons.length === 0) {
          return res.status(404).json({ message: 'Reason not found' });
      }

      res.json(reasons);
  } catch (err) {
      res.status(500).json({ message: err.message });
  }
});

classRouter.put("/api/update-class/:id", async (req, res) => {
  try {
    const classId = req.params.id;
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

    // Validate subject IDs
    if (subjectIds) {
      const subjects = await Subject.find({ _id: { $in: subjectIds } });
      if (subjects.length !== subjectIds.length) {
        return res.status(400).json({ message: "Some subjects were not found" });
      }
    }

    // Find the existing class
    const existingClass = await Class.findById(classId).exec();
    if (!existingClass) {
      return res.status(404).json({ message: "Class not found" });
    }

    // Determine new status
    const newStatus = status === 'rejected' ? 'pending' : status;

    // Update fields
    const updatedClass = await Class.findByIdAndUpdate(
      classId,
      {
        $set: {
          studentInfo: studentInfo || existingClass.studentInfo,
          schedule: schedule || existingClass.schedule,
          status: newStatus || existingClass.status,
          salary: salary || existingClass.salary,
          tutorId: tutorId || existingClass.tutorId,
          clientId: clientId || existingClass.clientId,
          form: form || existingClass.form,
          requirement: requirement || existingClass.requirement,
          address: address || existingClass.address,
          subjects: subjectIds || existingClass.subjects, // Update subjects only if subjectIds is provided
          updateDate: new Date(),
        },
      },
      { new: true } // Return the updated document
    ).exec();

    // Aggregate class details with subjects
    const classWithDetails = await Class.aggregate([
      {
        $match: { _id: new mongoose.Types.ObjectId(updatedClass._id) },
      },
      {
        $lookup: {
          from: "subjects",
          localField: "subjects",
          foreignField: "_id",
          as: "subjectDetails",
        },
      },
      {
        $unwind: {
          path: "$subjectDetails",
          preserveNullAndEmptyArrays: true,
        },
      },
      {
        $group: {
          _id: "$_id",
          studentInfo: { $first: "$studentInfo" },
          schedule: { $first: "$schedule" },
          status: { $first: "$status" },
          salary: { $first: "$salary" },
          tutorId: { $first: "$tutorId" },
          clientId: { $first: "$clientId" },
          form: { $first: "$form" },
          requirement: { $first: "$requirement" },
          address: { $first: "$address" },
          subjects: { $first: "$subjects" },
          subjectDetails: { $push: "$subjectDetails" },
          __v: { $first: "$__v" },
        },
      },
    ]);

    res.status(200).json(classWithDetails[0]);
  } catch (e) {
    console.log(e.message);
    res.status(500).json({ error: "Failed to update class" });
  }
});

module.exports = classRouter;
