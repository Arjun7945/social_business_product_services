import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getProductImages } from 'app/entities/product-image/product-image.reducer';
import { createEntity, getEntity, reset, updateEntity } from './fish-product.reducer';

export const FishProductUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productImages = useAppSelector(state => state.productImage.entities);
  const fishProductEntity = useAppSelector(state => state.fishProduct.entity);
  const loading = useAppSelector(state => state.fishProduct.loading);
  const updating = useAppSelector(state => state.fishProduct.updating);
  const updateSuccess = useAppSelector(state => state.fishProduct.updateSuccess);

  const handleClose = () => {
    navigate(`/fish-product${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProductImages({}));
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
    if (values.pricePerKg !== undefined && typeof values.pricePerKg !== 'number') {
      values.pricePerKg = Number(values.pricePerKg);
    }
    if (values.availableQuantity !== undefined && typeof values.availableQuantity !== 'number') {
      values.availableQuantity = Number(values.availableQuantity);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...fishProductEntity,
      ...values,
      image: productImages.find(it => it.id.toString() === values.image?.toString()),
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
          createdAt: displayDefaultDateTime(),
        }
      : {
          ...fishProductEntity,
          createdAt: convertDateTimeFromServer(fishProductEntity.createdAt),
          image: fishProductEntity?.image?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.fishProduct.home.createOrEditLabel" data-cy="FishProductCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.fishProduct.home.createOrEditLabel">Create or edit a FishProduct</Translate>
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
                  id="fish-product-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.fishProduct.name')}
                id="fish-product-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.fishProduct.pricePerKg')}
                id="fish-product-pricePerKg"
                name="pricePerKg"
                data-cy="pricePerKg"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.fishProduct.availableQuantity')}
                id="fish-product-availableQuantity"
                name="availableQuantity"
                data-cy="availableQuantity"
                type="text"
              />
              <UncontrolledTooltip target="availableQuantityLabel">
                <Translate contentKey="whatsappProductServiceProApp.fishProduct.help.availableQuantity" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.fishProduct.description')}
                id="fish-product-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 2000, message: translate('entity.validation.maxlength', { max: 2000 }) },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.fishProduct.isAvailable')}
                id="fish-product-isAvailable"
                name="isAvailable"
                data-cy="isAvailable"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.fishProduct.createdAt')}
                id="fish-product-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="fish-product-image"
                name="image"
                data-cy="image"
                label={translate('whatsappProductServiceProApp.fishProduct.image')}
                type="select"
              >
                <option value="" key="0" />
                {productImages
                  ? productImages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/fish-product" replace color="info">
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

export default FishProductUpdate;
