import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTeamMembers } from 'app/entities/team-member/team-member.reducer';
import { getEntities as getDeliveryZones } from 'app/entities/delivery-zone/delivery-zone.reducer';
import { DeliveryStatus } from 'app/shared/model/enumerations/delivery-status.model';
import { createEntity, getEntity, reset, updateEntity } from './delivery-person.reducer';

export const DeliveryPersonUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const teamMembers = useAppSelector(state => state.teamMember.entities);
  const deliveryZones = useAppSelector(state => state.deliveryZone.entities);
  const deliveryPersonEntity = useAppSelector(state => state.deliveryPerson.entity);
  const loading = useAppSelector(state => state.deliveryPerson.loading);
  const updating = useAppSelector(state => state.deliveryPerson.updating);
  const updateSuccess = useAppSelector(state => state.deliveryPerson.updateSuccess);
  const deliveryStatusValues = Object.keys(DeliveryStatus);

  const handleClose = () => {
    navigate(`/delivery-person${location.search}`);
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
    values.joinedAt = convertDateTimeToServer(values.joinedAt);

    const entity = {
      ...deliveryPersonEntity,
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
        }
      : {
          status: 'FREE',
          ...deliveryPersonEntity,
          joinedAt: convertDateTimeFromServer(deliveryPersonEntity.joinedAt),
          addedBy: deliveryPersonEntity?.addedBy?.id,
          zone: deliveryPersonEntity?.zone?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.deliveryPerson.home.createOrEditLabel" data-cy="DeliveryPersonCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.home.createOrEditLabel">
              Create or edit a DeliveryPerson
            </Translate>
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
                  id="delivery-person-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.deliveryPerson.name')}
                id="delivery-person-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.deliveryPerson.waPhoneNumber')}
                id="delivery-person-waPhoneNumber"
                name="waPhoneNumber"
                data-cy="waPhoneNumber"
                type="text"
                validate={{}}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.deliveryPerson.phoneNumber')}
                id="delivery-person-phoneNumber"
                name="phoneNumber"
                data-cy="phoneNumber"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.deliveryPerson.status')}
                id="delivery-person-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {deliveryStatusValues.map(deliveryStatus => (
                  <option value={deliveryStatus} key={deliveryStatus}>
                    {translate(`whatsappProductServiceProApp.DeliveryStatus.${deliveryStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.deliveryPerson.joinedAt')}
                id="delivery-person-joinedAt"
                name="joinedAt"
                data-cy="joinedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.deliveryPerson.isActive')}
                id="delivery-person-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                id="delivery-person-addedBy"
                name="addedBy"
                data-cy="addedBy"
                label={translate('whatsappProductServiceProApp.deliveryPerson.addedBy')}
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
                id="delivery-person-zone"
                name="zone"
                data-cy="zone"
                label={translate('whatsappProductServiceProApp.deliveryPerson.zone')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/delivery-person" replace color="info">
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

export default DeliveryPersonUpdate;
