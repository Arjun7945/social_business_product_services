import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { UserRole } from 'app/shared/model/enumerations/user-role.model';
import { AccountStatus } from 'app/shared/model/enumerations/account-status.model';
import { createEntity, getEntity, reset, updateEntity } from './removed-user.reducer';

export const RemovedUserUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const removedUserEntity = useAppSelector(state => state.removedUser.entity);
  const loading = useAppSelector(state => state.removedUser.loading);
  const updating = useAppSelector(state => state.removedUser.updating);
  const updateSuccess = useAppSelector(state => state.removedUser.updateSuccess);
  const userRoleValues = Object.keys(UserRole);
  const accountStatusValues = Object.keys(AccountStatus);

  const handleClose = () => {
    navigate(`/removed-user${location.search}`);
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
    if (values.originalId !== undefined && typeof values.originalId !== 'number') {
      values.originalId = Number(values.originalId);
    }
    if (values.locationLat !== undefined && typeof values.locationLat !== 'number') {
      values.locationLat = Number(values.locationLat);
    }
    if (values.locationLon !== undefined && typeof values.locationLon !== 'number') {
      values.locationLon = Number(values.locationLon);
    }
    values.joinedAt = convertDateTimeToServer(values.joinedAt);
    values.removedAt = convertDateTimeToServer(values.removedAt);
    if (values.orderHistoryId !== undefined && typeof values.orderHistoryId !== 'number') {
      values.orderHistoryId = Number(values.orderHistoryId);
    }
    if (values.distanceFromBusinessKm !== undefined && typeof values.distanceFromBusinessKm !== 'number') {
      values.distanceFromBusinessKm = Number(values.distanceFromBusinessKm);
    }

    const entity = {
      ...removedUserEntity,
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
      ? {
          joinedAt: displayDefaultDateTime(),
          removedAt: displayDefaultDateTime(),
        }
      : {
          role: 'CUSTOMER',
          status: 'ACCOUNT_REMOVED',
          ...removedUserEntity,
          joinedAt: convertDateTimeFromServer(removedUserEntity.joinedAt),
          removedAt: convertDateTimeFromServer(removedUserEntity.removedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.removedUser.home.createOrEditLabel" data-cy="RemovedUserCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.removedUser.home.createOrEditLabel">Create or edit a RemovedUser</Translate>
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
                  id="removed-user-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.originalId')}
                id="removed-user-originalId"
                name="originalId"
                data-cy="originalId"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.name')}
                id="removed-user-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.role')}
                id="removed-user-role"
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
                label={translate('whatsappProductServiceProApp.removedUser.whatsappNumber')}
                id="removed-user-whatsappNumber"
                name="whatsappNumber"
                data-cy="whatsappNumber"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.phoneNumber')}
                id="removed-user-phoneNumber"
                name="phoneNumber"
                data-cy="phoneNumber"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.address')}
                id="removed-user-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.locationLat')}
                id="removed-user-locationLat"
                name="locationLat"
                data-cy="locationLat"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.locationLon')}
                id="removed-user-locationLon"
                name="locationLon"
                data-cy="locationLon"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.joinedAt')}
                id="removed-user-joinedAt"
                name="joinedAt"
                data-cy="joinedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.removedAt')}
                id="removed-user-removedAt"
                name="removedAt"
                data-cy="removedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.reasonForRemoval')}
                id="removed-user-reasonForRemoval"
                name="reasonForRemoval"
                data-cy="reasonForRemoval"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.lastSessionData')}
                id="removed-user-lastSessionData"
                name="lastSessionData"
                data-cy="lastSessionData"
                type="textarea"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.status')}
                id="removed-user-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {accountStatusValues.map(accountStatus => (
                  <option value={accountStatus} key={accountStatus}>
                    {translate(`whatsappProductServiceProApp.AccountStatus.${accountStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.orderHistoryId')}
                id="removed-user-orderHistoryId"
                name="orderHistoryId"
                data-cy="orderHistoryId"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.distanceFromBusinessKm')}
                id="removed-user-distanceFromBusinessKm"
                name="distanceFromBusinessKm"
                data-cy="distanceFromBusinessKm"
                type="text"
              />
              <UncontrolledTooltip target="distanceFromBusinessKmLabel">
                <Translate contentKey="whatsappProductServiceProApp.removedUser.help.distanceFromBusinessKm" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.removedUser.isPincodeValid')}
                id="removed-user-isPincodeValid"
                name="isPincodeValid"
                data-cy="isPincodeValid"
                check
                type="checkbox"
              />
              <UncontrolledTooltip target="isPincodeValidLabel">
                <Translate contentKey="whatsappProductServiceProApp.removedUser.help.isPincodeValid" />
              </UncontrolledTooltip>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/removed-user" replace color="info">
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

export default RemovedUserUpdate;
