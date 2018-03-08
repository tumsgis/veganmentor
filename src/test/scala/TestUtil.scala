object TestUtil {
  def noEmptySlotsBelowZero: Boolean =
    !MongoDbRepo.getAllMentors.exists(_.emptySlots < 0)
}
