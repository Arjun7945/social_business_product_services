import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './delivery-zone.reducer';

export const DeliveryZoneUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const deliveryZoneEntity = useAppSelector(state => state.deliveryZone.entity);
  const loading = useAppSelector(state => state.deliveryZone.loading);
  const updating = useAppSelector(state => state.deliveryZone.updating);
  const updateSuccess = useAppSelector(state => state.deliveryZone.updateSuccess);

  const handleClose = () => {
    navigate(`/delivery-zone${location.search}`);
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
      ...deliveryZoneEntity,
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
          ...deliveryZoneEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.deliveryZone.home.createOrEditLabel" data-cy="DeliveryZoneCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.deliveryZone.home.createOrEditLabel">
              Create or edit a DeliveryZone
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
                  id="delivery-zone-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.deliveryZone.zoneName')}
                id="delivery-zone-zoneName"
                name="zoneName"
                data-cy="zoneName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <UncontrolledTooltip target="zoneNameLabel">
                <Translate contentKey="whatsappProductServiceProApp.deliveryZone.help.zoneName" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.deliveryZone.pincode')}
                id="delivery-zone-pincode"
                name="pincode"
                data-cy="pincode"
                type="text"
              />
              <UncontrolledTooltip target="pincodeLabel">
                <Translate contentKey="whatsappProductServiceProApp.deliveryZone.help.pincode" />
              </UncontrolledTooltip>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/delivery-zone" replace color="info">
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

export default DeliveryZoneUpdate;
