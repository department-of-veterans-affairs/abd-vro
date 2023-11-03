"""
Script for programmatically detecting changes to the database schema and
creating them in the VRO flywheel migrations folder, so that the db-init
service can properly CRUD postgres tables
"""

import os
import subprocess
import sys

from alembic import command
from alembic.config import Config
from alembic.util.exc import AutogenerateDiffsDetected


# def are_database_changes():
#     cmd = ['alembic', 'check']  # Command to execute
#     process = subprocess.Popen(cmd, stdout=subprocess.PIPE)  # Execute the command
#     output, error = process.communicate()
#     return 'New upgrade operations detected' in output.decode('utf-8')


def generate_new_migration(alembic_cfg):
    revision_script = command.revision(alembic_cfg, autogenerate=True, message='automated migration message')
    return revision_script.path

def get_revision_hash(migration_filepath):
    filename = os.path.basename(migration_filepath)
    return filename.split('_')[0]

def get_version_as_vars(version_str: str):
    try:
        major, minor, micro = version_str.split('.')
    except ValueError:
        try:
            major, minor = version_str.split('.')
            micro = 0
        except ValueError:
            major = version_str
            minor = 0
            micro = 0
    return int(major), int(minor), int(micro)

def is_higher_migration(first_version_num: str, second_version_num: str):
    first_major, first_minor, first_micro = get_version_as_vars(first_version_num)
    second_major, second_minor, second_micro = get_version_as_vars(second_version_num)

    if first_major > second_major:
        return True
    elif first_major == second_major:
        if first_minor > second_minor:
            return True
        elif first_minor == second_minor:
            if first_micro > second_micro:
                return True
    return False

def increment_version_number(version_number):
    major, minor, micro = get_version_as_vars(version_number)
    micro = int(micro) + 1
    return f'{major}.{minor}.{micro}'

def generate_flyway_migration_version():
    """
    '../../db-init/src/main/resources/database/migrations
    """
    migration_files = os.listdir('../../db-init/src/main/resources/database/migrations')
    migration_files = [f for f in migration_files if f.startswith('V')]
    latest_migration_number = None
    for migration_file in migration_files:
        migration_number_only_str = migration_file[1:].split('_')[0]
        if latest_migration_number is None or is_higher_migration(migration_number_only_str, latest_migration_number):
            latest_migration_number = migration_number_only_str
    return increment_version_number(latest_migration_number)


def generate_flyway_migration(alembic_cfg, new_migration_path):
    """
    alembic upgrade <revision-hash> --sql > new_sql_file.sql
    mv new_sql_file.sql abd-vro/db-init/src/main/resources/database/migrations/domain_cc_<hash>_migration.sql
    """
    revision_hash = get_revision_hash(new_migration_path)
    cmd = ['alembic', 'upgrade', revision_hash, '--sql', '>', 'new_sql_file.sql']
    # process = subprocess.Popen(cmd, stdout=subprocess.PIPE)  # Execute the command
    output = subprocess.getoutput(cmd)
    # process.communicate()


    # upgrade_response = command.upgrade(alembic_cfg, revision_hash, sql=True)
    # print(f'upgrade_response: {upgrade_response}')
    print('writing to new_sql_file.sql')
    with open('new_sql_file.sql', 'w') as f:
        f.write(output)

    # new_sql_migration_filename = f'abd-vro/db-init/src/main/resources/database/migrations/domain_cc_{revision_hash}_migration.sql'
    print('generating new .sql migration filename')
    new_migration_name = f'{generate_flyway_migration_version()}_domain_cc_{revision_hash}_migration.sql'
    new_sql_migration_filepath = os.path.join('.', f'../../db-init/src/main/resources/database/migrations/{new_migration_name}')
    cmd = ['mv', 'new_sql_file.sql', new_sql_migration_filepath]
    process = subprocess.Popen(cmd, stdout=subprocess.PIPE)  # Execute the command
    process.communicate()

def re_run_db_init():
    """
    docker restart vro-db-init-1
    """
    cmd = ['docker', 'restart', 'vro-db-init-1']
    process = subprocess.Popen(cmd, stdout=subprocess.PIPE)  # Execute the command
    process.communicate()

def main():
    # check if there are any changes to apply
    os.chdir('cc-app')
    alembic_cfg = Config("./alembic.ini")
    try:
        command.check(alembic_cfg)
    except AutogenerateDiffsDetected:
        print('handling database changes')

        print('generate_new_migration...')
        new_migration_path = generate_new_migration(alembic_cfg)
        print('generate_flyway_migration...')
        generate_flyway_migration(alembic_cfg, new_migration_path)
        # os.chdir('../..')
        print('re_run_db_init...')
        re_run_db_init()
    else:
        print('no new changes to apply')


if __name__ == '__main__':
    main()
