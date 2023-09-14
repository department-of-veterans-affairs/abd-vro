import logging

import hoppy_service
from merge_job import MergeJob
from response_exception import ResponseException


def merge_end_products(job: MergeJob):
    job.next_state()
    temporary_station_of_jurisdiction = make_hoppy_request(job=job,
                                                           body={"claim_id": job.pending_claim_id,
                                                                 "SOJ": 398},
                                                           hoppy_client=hoppy_service.set_temp_station_of_jurisdiction)
    logging.info(temporary_station_of_jurisdiction)

    job.next_state()
    pending_contentions = make_hoppy_request(job=job,
                                             body={"claim_id": job.pending_claim_id},
                                             hoppy_client=hoppy_service.get_claim_contentions)
    logging.info(pending_contentions)

    job.next_state()
    supp_contentions = make_hoppy_request(job=job,
                                          body={"claim_id": job.supp_claim_id},
                                          hoppy_client=hoppy_service.get_claim_contentions)
    logging.info(supp_contentions)

    job.next_state()
    merged_contentions = merge_contentions(job, pending_contentions, supp_contentions)
    logging.info(merged_contentions)

    job.next_state()
    updated_contention = make_hoppy_request(job=job,
                                            body=merged_contentions,
                                            hoppy_client=hoppy_service.update_contentions)
    logging.info(updated_contention)

    job.next_state()
    cancellation = make_hoppy_request(job=job,
                                      body={"claim_id": job.supp_claim_id},
                                      hoppy_client=hoppy_service.cancel_claim)
    logging.info(cancellation)

    job.next_state()


def make_hoppy_request(job, body, hoppy_client):
    try:
        return hoppy_client.request(body)
    except ResponseException as e:
        logging.warning(
            f"event=errorProcessingJob job_id={job.job_id} correlation_id={e.correlation_id} error=\"{e.message}\"")
        job.error(e.message)
        raise e


def merge_contentions(job, pending_contentions, supp_contentions):
    return {"updated": "contentions"}
