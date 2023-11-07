"""add fake model for Mason's testing

Revision ID: 548de336b7df
Revises: 
Create Date: 2023-10-18 16:00:36.060304

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '548de336b7df'
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('mason_model_not_real_model_delete_me',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('vets_api_claim_id', sa.Integer(), nullable=True),
    sa.Column('vets_api_form526_submission_id', sa.Integer(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_mason_model_not_real_model_delete_me_id'), 'mason_model_not_real_model_delete_me', ['id'], unique=False)
    op.create_index(op.f('ix_mason_model_not_real_model_delete_me_vets_api_claim_id'), 'mason_model_not_real_model_delete_me', ['vets_api_claim_id'], unique=True)
    op.drop_index('ix_contentions_id', table_name='contentions')
    op.drop_table('contentions')
    op.drop_index('ix_claim_id', table_name='claim')
    op.drop_index('ix_claim_vets_api_claim_id', table_name='claim')
    op.drop_table('claim')
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('claim',
    sa.Column('id', sa.INTEGER(), server_default=sa.text("nextval('claim_id_seq'::regclass)"), autoincrement=True, nullable=False),
    sa.Column('vets_api_claim_id', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.Column('vets_api_form526_submission_id', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.PrimaryKeyConstraint('id', name='claim_pkey'),
    postgresql_ignore_search_path=False
    )
    op.create_index('ix_claim_vets_api_claim_id', 'claim', ['vets_api_claim_id'], unique=False)
    op.create_index('ix_claim_id', 'claim', ['id'], unique=False)
    op.create_table('contentions',
    sa.Column('id', sa.INTEGER(), autoincrement=True, nullable=False),
    sa.Column('diagnostic_code', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.Column('classification_code', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.Column('claim_id', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.ForeignKeyConstraint(['claim_id'], ['claim.id'], name='contentions_claim_id_fkey'),
    sa.PrimaryKeyConstraint('id', name='contentions_pkey')
    )
    op.create_index('ix_contentions_id', 'contentions', ['id'], unique=False)
    op.drop_index(op.f('ix_mason_model_not_real_model_delete_me_vets_api_claim_id'), table_name='mason_model_not_real_model_delete_me')
    op.drop_index(op.f('ix_mason_model_not_real_model_delete_me_id'), table_name='mason_model_not_real_model_delete_me')
    op.drop_table('mason_model_not_real_model_delete_me')
    # ### end Alembic commands ###