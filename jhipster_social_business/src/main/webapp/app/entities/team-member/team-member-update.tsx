import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { UserRole } from 'app/shared/model/enumerations/user-role.model';
import { createEntity, getEntity, reset, updateEntity } from './team-member.reducer';

export const TeamMemberUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const teamMemberEntity = useAppSelector(state => state.teamMember.entity);
  const loading = useAppSelector(state => state.teamMember.loading);
  const updating = useAppSelector(state => state.teamMember.updating);
  const updateSuccess = useAppSelector(state => state.teamMember.updateSuccess);
  const userRoleValues = Object.keys(UserRole);

  const handleClose = () => {
    navigate(`/team-member${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...teamMemberEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          role: 'CUSTOMER',
          ...teamMemberEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.teamMember.home.createOrEditLabel" data-cy="TeamMemberCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.teamMember.home.createOrEditLabel">Create or edit a TeamMember</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="team-member-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.teamMember.name')}
                id="team-member-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.teamMember.waPhoneNumber')}
                id="team-member-waPhoneNumber"
                name="waPhoneNumber"
                data-cy="waPhoneNumber"
                type="text"
                validate={{}}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.teamMember.phoneNumber')}
                id="team-member-phoneNumber"
                name="phoneNumber"
                data-cy="phoneNumber"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.teamMember.role')}
                id="team-member-role"
                name="role"
                data-cy="role"
                type="select"
              >
                {userRoleValues.map(userRole => (
                  <option value={userRole} key={userRole}>
                    {translate(`whatsappProductServiceProApp.UserRole.${userRole}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.teamMember.isActive')}
                id="team-member-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/team-member" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TeamMemberUpdate;
