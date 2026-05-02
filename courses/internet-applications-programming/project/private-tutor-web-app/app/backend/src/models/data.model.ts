import mongoose from "mongoose"

const dataSchema = new mongoose.Schema(
    {
        subjects: Array,
        studentAges: Array,
        schoolTypes: Array,
        primarySchoolGrades: Array,
        secondarySchoolGrades: Array
    },
    {
        versionKey: false
    }
);

const DataModel = mongoose.model("DataModel", dataSchema, "data")
export default DataModel