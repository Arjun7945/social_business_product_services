import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTeamMembers } from 'app/entities/team-member/team-member.reducer';
import { getEntities as getDeliveryZones } from 'app/entities/delivery-zone/delivery-zone.reducer';
import { UserRole } from 'app/shared/model/enumerations/user-role.model';
import { createEntity, getEntity, reset, updateEntity } from './customer.reducer';

export const CustomerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const teamMembers = useAppSelector(state => state.teamMember.entities);
  const deliveryZones = useAppSelector(state => state.deliveryZone.entities);
  const customerEntity = useAppSelector(state => state.customer.entity);
  const loading = useAppSelector(state => state.customer.loading);
  const updating = useAppSelector(state => state.customer.updating);
  const updateSuccess = useAppSelector(state => state.customer.updateSuccess);
  const userRoleValues = Object.keys(UserRole);

  const handleClose = () => {
    navigate(`/customer${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTeamMembers({}));
    dispatch(getDeliveryZones({}));
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
    if (values.locationLat !== undefined && typeof values.locationLat !== 'number') {
      values.locationLat = Number(values.locationLat);
    }
    if (values.locationLon !== undefined && typeof values.locationLon !== 'number') {
      values.locationLon = Number(values.locationLon);
    }
    if (values.distanceFromBusinessKm !== undefined && typeof values.distanceFromBusinessKm !== 'number') {
      values.distanceFromBusinessKm = Number(values.distanceFromBusinessKm);
    }
    values.joinedAt = convertDateTimeToServer(values.joinedAt);
    values.lastInteractionAt = convertDateTimeToServer(values.lastInteractionAt);

    const entity = {
      ...customerEntity,
      ...values,
      addedBy: teamMembers.find(it => it.id.toString() === values.addedBy?.toString()),
      zone: deliveryZones.find(it => it.id.toString() === values.zone?.toString()),
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
          lastInteractionAt: displayDefaultDateTime(),
        }
      : {
          role: 'CUSTOMER',
          ...customerEntity,
          joinedAt: convertDateTimeFromServer(customerEntity.joinedAt),
          lastInteractionAt: convertDateTimeFromServer(customerEntity.lastInteractionAt),
          addedBy: customerEntity?.addedBy?.id,
          zone: customerEntity?.zone?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.customer.home.createOrEditLabel" data-cy="CustomerCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.customer.home.createOrEditLabel">Create or edit a Customer</Translate>
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
                  id="customer-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.waPhoneNumber')}
                id="customer-waPhoneNumber"
                name="waPhoneNumber"
                data-cy="waPhoneNumber"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.name')}
                id="customer-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.phoneNumber')}
                id="customer-phoneNumber"
                name="phoneNumber"
                data-cy="phoneNumber"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.locationLat')}
                id="customer-locationLat"
                name="locationLat"
                data-cy="locationLat"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.locationLon')}
                id="customer-locationLon"
                name="locationLon"
                data-cy="locationLon"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.address')}
                id="customer-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.distanceFromBusinessKm')}
                id="customer-distanceFromBusinessKm"
                name="distanceFromBusinessKm"
                data-cy="distanceFromBusinessKm"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.isPincodeValid')}
                id="customer-isPincodeValid"
                name="isPincodeValid"
                data-cy="isPincodeValid"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.role')}
                id="customer-role"
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
                label={translate('whatsappProductServiceProApp.customer.joinedAt')}
                id="customer-joinedAt"
                name="joinedAt"
                data-cy="joinedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.customer.lastInteractionAt')}
                id="customer-lastInteractionAt"
                name="lastInteractionAt"
                data-cy="lastInteractionAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="customer-addedBy"
                name="addedBy"
                data-cy="addedBy"
                label={translate('whatsappProductServiceProApp.customer.addedBy')}
                type="select"
              >
                <option value="" key="0" />
                {teamMembers
                  ? teamMembers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="customer-zone"
                name="zone"
                data-cy="zone"
                label={translate('whatsappProductServiceProApp.customer.zone')}
                type="select"
              >
                <option value="" key="0" />
                {deliveryZones
                  ? deliveryZones.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.zoneName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/customer" replace color="info">
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

export default CustomerUpdate;
