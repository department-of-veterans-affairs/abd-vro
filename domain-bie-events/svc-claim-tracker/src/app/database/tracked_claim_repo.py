from app.database.database import Database
from app.model.tracked_claim import TrackedClaim


class TrackedClaimRepo(Database[TrackedClaim]):
    pass


tracked_claim_repo = TrackedClaimRepo(model=TrackedClaim)
